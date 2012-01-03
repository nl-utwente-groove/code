<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n3">
            <attr name="layout">
                <string>238 186 34 46</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>104 190 37 76</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>182 91 34 46</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>47 91 34 46</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>201 293 34 31</string>
            </attr>
        </node>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:A2</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>flag:a</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>flag:a2</string>
            </attr>
        </edge>
        <edge to="n1" from="n2">
            <attr name="label">
                <string>a2ToB</string>
            </attr>
        </edge>
        <edge to="n1" from="n2">
            <attr name="label">
                <string>aToB</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>aToB</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:A1</string>
            </attr>
        </edge>
    </graph>
</gxl>
