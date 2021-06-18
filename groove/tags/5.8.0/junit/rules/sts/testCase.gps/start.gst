<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n5">
            <attr name="layout">
                <string>135 121 50 33</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>61 121 44 37</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>135 179 44 37</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>182 218 44 37</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>204 157 46 50</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>87 64 60 33</string>
            </attr>
        </node>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>bool:true</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>real:2.0</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>real:1.5</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>real:2.0</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>flag:final</string>
            </attr>
        </edge>
        <edge to="n3" from="n1">
            <attr name="label">
                <string>has</string>
            </attr>
        </edge>
        <edge to="n2" from="n1">
            <attr name="label">
                <string>has</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:Thing</string>
            </attr>
        </edge>
        <edge to="n4" from="n0">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge to="n5" from="n0">
            <attr name="label">
                <string>b</string>
            </attr>
        </edge>
    </graph>
</gxl>
