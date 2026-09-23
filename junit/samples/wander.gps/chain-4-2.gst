<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="chain-4-2">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>40 40 30 30</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>130 40 30 30</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>220 40 30 30</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>310 40 30 30</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>40 110 30 30</string>
            </attr>
        </node>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>type:Hub</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>let:leaves=0</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>let:tokens=0</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>let:moves=0</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>type:Leaf</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>flag:token</string>
            </attr>
        </edge>
        <edge from="n0" to="n1">
            <attr name="label">
                <string>to</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>type:Leaf</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>flag:token</string>
            </attr>
        </edge>
        <edge from="n0" to="n2">
            <attr name="label">
                <string>to</string>
            </attr>
        </edge>
        <edge from="n1" to="n2">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>type:Leaf</string>
            </attr>
        </edge>
        <edge from="n0" to="n3">
            <attr name="label">
                <string>to</string>
            </attr>
        </edge>
        <edge from="n2" to="n3">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>type:Leaf</string>
            </attr>
        </edge>
        <edge from="n0" to="n4">
            <attr name="label">
                <string>to</string>
            </attr>
        </edge>
        <edge from="n3" to="n4">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
    </graph>
</gxl>
