<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="createNodeWithNac-0">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n8">
            <attr name="layout">
                <string>410 238 34 46</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>70 234 180 31</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>489 241 34 46</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>406 182 34 46</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>485 183 34 46</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>484 112 285 31</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>407 111 34 46</string>
            </attr>
        </node>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>These nodes violate the NAC</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>rem:</string>
            </attr>
        </edge>
        <edge to="n8" from="n2">
            <attr name="label">
                <string>this</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>This target node does not have the forbidden C</string>
            </attr>
        </edge>
        <edge to="n0" from="n8">
            <attr name="label">
                <string>b</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>rem:</string>
            </attr>
        </edge>
        <edge to="n4" from="n3">
            <attr name="label">
                <string>b</string>
            </attr>
        </edge>
        <edge to="n1" from="n7">
            <attr name="label">
                <string>this</string>
            </attr>
        </edge>
        <edge to="n4" from="n8">
            <attr name="label">
                <string>b</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n3" from="n2">
            <attr name="label">
                <string>this</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
    </graph>
</gxl>
